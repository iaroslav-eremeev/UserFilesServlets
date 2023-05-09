package servlets;

import hibernate.DAO;
import model.User;
import model.UserFile;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import util.UnicodeSetup;

import javax.persistence.Query;
import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;
import java.util.Objects;

@WebServlet("/file")
public class FileServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UnicodeSetup.setUnicode(req, resp);
        PrintWriter writer = resp.getWriter();
        if (ServletFileUpload.isMultipartContent(req)) {
            // Create a factory for disk-based file items
            DiskFileItemFactory factory = new DiskFileItemFactory();

            // Configure a repository (to ensure a secure temp location is used)
            ServletContext servletContext = this.getServletConfig().getServletContext();
            File repository = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
            factory.setRepository(repository);

            String uploadPath = "C:\\users_files";
            File tempDir = new File(uploadPath);
            if (!tempDir.exists()) {
                tempDir.mkdirs();
            }

            // Create a new file upload handler
            ServletFileUpload upload = new ServletFileUpload(factory);

            // Parse the request
            try {
                List<FileItem> formItems = upload.parseRequest(req);
                if (formItems != null && formItems.size() > 0) {
                    String userId = null;
                    FileItem fileItem = null;
                    for (FileItem item : formItems) {
                        if (item.isFormField() && "userId".equals(item.getFieldName())) {
                            userId = item.getString();
                        } else if (!item.isFormField()) {
                            fileItem = item;
                        }
                    }
                    if (userId != null && fileItem != null){
                        String fileName = fileItem.getName();
                        User user = (User) DAO.getObjectById(Long.parseLong(userId), User.class);
                        if (user != null){
                            // Add file to database
                            UserFile userFile = new UserFile(fileName, user);
                            DAO.addObject(userFile);
                            // Generate unique serverFilename and update file in the database
                            String fileExtension = fileName.substring(fileName.lastIndexOf("."));
                            String userFileId = String
                                    .valueOf(Objects.requireNonNull(DAO.getAllObjects(UserFile.class))
                                            .size());
                            String serverFilename = userFileId + fileExtension;
                            userFile.setServerFilename(serverFilename);
                            DAO.updateObject(userFile);
                            // Writing item to a file
                            fileItem.write(new File(tempDir, serverFilename));
                            writer.println("File "
                                    + fileName
                                    + " has been successfully uploaded under the name "
                                    + serverFilename);
                            DAO.closeOpenedSession();
                        }
                        else {
                            writer.println("There is no user with such id!");
                            resp.setStatus(400);
                        }
                    }
                    else {
                        System.out.println("Incorrect request parameters");
                        resp.setStatus(400);
                    }

                }
                else {
                    System.out.println("Incorrect request. A userId and a file are needed");
                    resp.setStatus(400);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
                writer.println(e.getMessage());
                resp.setStatus(400);
            }
        } else {
            writer.println("Incorrect request. This is not a multipart request");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UnicodeSetup.setUnicode(req, resp);
        ServletContext cntx = req.getServletContext();
        // The file will be saved to desktop
        File dir = new File("C:\\users_files");
        String userId = req.getParameter("userId");
        String filename = req.getParameter("filename");

        if (userId != null && filename == null){
            User user = (User) DAO.getObjectById(Long.parseLong(userId), User.class);
            assert user != null;
            List<UserFile> userFiles = user.getUserFiles();
            resp.getWriter().println("All the files stored in the database for user " + user.getName() + ":");
            resp.getWriter().println(userFiles);
            DAO.closeOpenedSession();
        }
        else if (userId != null){
            User user = (User) DAO.getObjectById(Long.parseLong(userId), User.class);
            assert user != null;
            UserFile userFile = (UserFile) DAO.getObjectByParam("filename", filename, UserFile.class);
            if (userFile == null || !userFile.getUser().getLogin().equals(user.getLogin())) {
                resp.getWriter().println("File not found");
                resp.setStatus(400);
                return;
            }
            String mime = cntx.getMimeType(filename);
            if (mime == null) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().println("Incorrect type file");
                resp.setStatus(400);
                return;
            }
            String serverFilename = userFile.getServerFilename();
            File file = new File(dir + File.separator + serverFilename);
            System.out.println(file);
            try (FileInputStream in = new FileInputStream(file);
                 OutputStream out = resp.getOutputStream()) {
                resp.setContentType(mime);
                resp.setContentLength((int) file.length());
                long length = in.transferTo(out);
                System.out.println("Bytes transferred: " + length);
            }
            catch (FileNotFoundException e){
                resp.getWriter().println("Incorrect file name");
                resp.setStatus(400);
            }
            catch (IOException e){
                resp.getWriter().println("File Error!");
                resp.setStatus(400);
            }
        }
        else {
            resp.getWriter().println("Incorrect request");
            resp.setStatus(400);
        }
    }
}
