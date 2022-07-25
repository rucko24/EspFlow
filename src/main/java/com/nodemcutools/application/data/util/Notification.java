package com.nodemcutools.application.data.util;

import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.NotificationVariant;
import lombok.extern.log4j.Log4j2;

import java.util.Objects;

/**
 * @author rubn
 */
@Log4j2
public class Notification {


    public static Text builder() {
        return new InnerBuilder();
    }

    /**
     * 1
     */
    public interface Text {
        Position withText(final String text);
    }

    /**
     * 2
     */
    public interface Position {
        Duration withPosition(final com.vaadin.flow.component.notification.Notification.Position position);
    }

    /**
     * 3
     */
    public interface Duration {
        WithIcon withDuration(final int duration);
    }

    /**
     * 4
     */
    public interface WithIcon {
        WithNotificationVariant withIcon(final VaadinIcon icon);
    }

    /**
     * 5
     */
    public interface WithNotificationVariant {
        Build withThemeVariant(final NotificationVariant notificationVariant);
    }

    /**
     * 6
     */
    public interface Build extends IBuilder<com.vaadin.flow.component.notification.Notification> {
    }

    public static class InnerBuilder implements Text, Position, Duration, WithIcon, WithNotificationVariant, Build {
        private com.vaadin.flow.component.notification.Notification.Position position;
        private VaadinIcon icon;
        private String text;
        private Integer duration;
        private NotificationVariant notificationVariant;

        @Override
        public Position withText(String text) {
            Objects.requireNonNull(text, () -> "Text must not be null");
            this.text = text;
            return this;
        }

        @Override
        public Duration withPosition(com.vaadin.flow.component.notification.Notification.Position position) {
            this.position = position;
            return this;
        }

        @Override
        public WithIcon withDuration(int duration) {
            this.duration = duration;
            return this;
        }

        @Override
        public WithNotificationVariant withIcon(VaadinIcon icon) {
            Objects.requireNonNull(icon, () -> "icon must not be null");
            this.icon = icon;
            return this;
        }

        @Override
        public Build withThemeVariant(NotificationVariant notificationVariant) {
            this.notificationVariant = notificationVariant;
            return this;
        }

        @Override
        public com.vaadin.flow.component.notification.Notification make() {
            final com.vaadin.flow.component.notification.Notification n = new com.vaadin.flow.component.notification.Notification();
            n.addComponentAtIndex(0, new Icon(icon));
            n.add(" " + text);
            n.setPosition(position);
            n.setDuration(duration);
            n.addThemeVariants(this.notificationVariant);
            n.open();
            return n;
        }

    }
}