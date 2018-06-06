package util;

import com.mega.mailserver.util.EmailUtils;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class EmailUtilsTest {

    @Test
    public void testValidEmailName() {
        String emailName = "taiberium.monster";

        boolean isValid = EmailUtils.isValidEmailName(emailName);

        assertThat(isValid).isEqualTo(true);
    }

    @Test
    public void testInvalidEmailName() {
        List<String> emailName = asList("мама", "orck@mail", "@mack.ru", "orda@lolka", "nita..nit",".nitanit");

        List<Boolean> answers = emailName.stream().map(EmailUtils::isValidEmail).collect(Collectors.toList());

        answers.forEach(answer -> assertThat(answer).isEqualTo(false));
    }

    @Test
    public void testValidEmail() {
        String emailName = "taiberium.monster@mail.ru";

        boolean isValid = EmailUtils.isValidEmail(emailName);

        assertThat(isValid).isEqualTo(true);
    }

    @Test
    public void testInvalidEmail() {
        List<String> emailName = asList(
                "nit()@mail.ru",
                "nit..nig@mail.ru",
                "nit@.mail.ru",
                "мама@mail.ru",
                "orck@mail@email.ru",
                "orck@.mail.ru",
                "orck@mail.ru.",
                ".orck@mail.ru",
                "@mack.ru",
                "orda@lolka"
        );

        List<Boolean> answers = emailName.stream().map(EmailUtils::isValidEmail).collect(Collectors.toList());

        answers.forEach(answer -> assertThat(answer).isEqualTo(false));
    }

}
