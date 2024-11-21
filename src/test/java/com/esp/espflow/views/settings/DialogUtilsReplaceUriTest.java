package com.esp.espflow.views.settings;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DialogUtilsReplaceUriTest {

    @Test
    @DisplayName("When the ref settings do not have a public-information fragment")
    void settings_without_uri_fragment() {
        String pathEmpty = "/";
        String ref = "settings";

        String resultPath = DialogUtilsReplaceUri.INSTANCE.replaceOrConcatFragment(pathEmpty, ref, "public-information");

        assertThat(resultPath).isEqualTo("/#settings/public-information");
    }

    @Test
    @DisplayName("When the ref settings is with public-information fragment and must not be repeated")
    void settings_with_uri_fragment() {
        String pathEmpty = "/";
        String ref = "settings/public-information";

        String resultPath = DialogUtilsReplaceUri.INSTANCE.replaceOrConcatFragment(pathEmpty, ref, "public-information");

        assertThat(resultPath).isEqualTo("/#settings/public-information");
    }

    @Test
    @DisplayName("When the ref settings is with contact-information fragment but we want to replace it with public-information fragment")
    void settings_with_uri_and_distints() {
        String pathEmpty = "/";
        String ref = "settings/contact-information";

        String resultPath = DialogUtilsReplaceUri.INSTANCE.replaceOrConcatFragment(pathEmpty, ref, "public-information");

        assertThat(resultPath).isEqualTo("/#settings/public-information");
    }

    @Test
    @DisplayName("Parse uri when settings/xxxxxx to create form content")
    void parseUriToCreateTheContentForm() {

        String settings = "settings/password";

        final String actual = DialogUtilsReplaceUri.INSTANCE.parseUriToCreateTheContentForm(settings);

        assertThat(actual).isEqualTo("password");
    }

    @Test
    @DisplayName("Parse uri like /#settings/notifications to create form content")
    void parseUriToCreateTheContentFormForNotifications() {

        String settings = "/#settings/notifications";

        final String actual = DialogUtilsReplaceUri.INSTANCE.parseUriToCreateTheContentForm(settings);

        assertThat(actual).isEqualTo("notifications");
    }

    @Test
    @DisplayName("Parseo de uri cuando es settings solo, para crear el contenido del form")
    void parseUriToCreateTheContentForm_settings_without_path() {

        final String settings = "settings";

        final String actual = DialogUtilsReplaceUri.INSTANCE.parseUriToCreateTheContentForm(settings);

        assertThat(actual).isEqualTo("settings");
    }

}