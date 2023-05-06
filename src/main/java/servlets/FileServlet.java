package servlets;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import util.UnicodeSetup;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

@WebServlet("/file")
public class FileServlet extends HttpServlet {

    /**
     * Написать сервлет FileServlet метод POST который принимает id пользователя и файл, добавляет информацию об этом файле в БД
     * и сохраняет файл на сервере
     * Таблица UserFiles состоит из колонок: id, filename, serverFilename, user_id.
     * Для одного пользователя файлы повторяться не могут.
     * Так как у разных пользователей названия файлов могут быть одинаковые, то нужно на сервер их сохранять по следующему механизму:
     * 1. Принять файл на сервер
     * 2. Добавить информацию в БД об этом файле, кроме колонки serverFilename
     * 3. После того, как база присвоит id, serverFilename должен быть равен "id.расширение, которое было у файла изначально"
     * 4. Произвести обновление колонки serverFilename, исходя из пункта 3
     * 5. Произвести сохранение файла под именем serverFilename
     */
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
                    for (FileItem item : formItems) {
                        if (!item.isFormField()) {
                            String fileName = item.getName();
                            item.write(new File(tempDir, fileName));
                            writer.println("File "
                                    + fileName + " has uploaded successfully!");
                        } else {
                            String name = item.getFieldName();
                            String value = item.getString();
                            switch (name) {
                                case "note" -> {
                                    System.out.println(value);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
                writer.println(e.getMessage());
                resp.setStatus(400);
            }
        } else {
            writer.println("No multipart");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");

        ServletContext cntx = req.getServletContext();
        File dir = new File("C:\\users_files");
        String filename = req.getParameter("filename");
        String mime = cntx.getMimeType(filename);
        System.out.println(mime);
        if (mime == null) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println("Incorrect type file");
            resp.setStatus(400);
            return;
        }

        File file = new File(dir + File.separator + filename);

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



}
