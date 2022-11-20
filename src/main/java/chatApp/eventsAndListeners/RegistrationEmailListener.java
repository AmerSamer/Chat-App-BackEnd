package chatApp.eventsAndListeners;
import java.util.UUID;

import chatApp.entities.User;
import chatApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;


@Component
public class RegistrationEmailListener implements ApplicationListener<OnRegistrationSuccessEvent> {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageSource messages;

//    @Autowired
//    private MailSender mailSender;

    @Override
    public void onApplicationEvent(OnRegistrationSuccessEvent event) {
        this.confirmRegistration(event);

    }

    private void confirmRegistration(OnRegistrationSuccessEvent event) {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        userService.createVerificationToken(user,token);

        String recipient = user.getEmail();
        String subject = "Registration Confirmation";
        String url
                = event.getAppUrl() + "/confirmRegistration?token=" + token;
        String message = messages.getMessage("message.registrationSuccessConfimationLink", null, event.getLocale());

//        SimpleMailMessage email = new SimpleMailMessage();
//        email.setTo(recipient);
//        email.setSubject(subject);
//        email.setText(message + "http://localhost:8080" + url);
//        System.out.println(url);
//        mailSender.send(email);

    }


}
