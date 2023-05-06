package servlets;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

@WebServlet("/file")
public class FileServlet extends HttpServlet {

    /**
     * Написать сервлет FileServlet метод POST который принимает id пользователя и файл, добавляет информацию об этом файле в БД
     * и сохраняет файл на сервере Таблица UserFiles состоит из колонок: id, filename, serverFilename, user_id.
     * Для одного пользователя файлы повторяться не могут.
     * Так как у разных пользователей названия файлов могут быть одинаковые, то нужно на сервер их сохранять по следующему механизму:
     * 1. Принять файл на сервер
     * 2. Добавить информацию в БД об этом файле, кроме колонки serverFilename
     * 3. После того, как база присвоит id, serverFilename должен быть равен "id.расширение, которое было у файла изначально"
     * 4. Произвести обновление колонки serverFilename, исходя из пункта 3
     * 5. Произвести сохранение файла под именем serverFilename
     */

}
