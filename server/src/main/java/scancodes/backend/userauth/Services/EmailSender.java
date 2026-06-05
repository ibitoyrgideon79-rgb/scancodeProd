package scancodes.backend.userauth.Services;

public interface EmailSender {

    void send(String to, String subject, String body);
}