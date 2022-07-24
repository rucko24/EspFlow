package com.nodemcutools.application.data.util;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;

/**
 * Show notificatins with Error
 */
public interface NotificationsUtils {

    /**
     * Show errors with Notifications
     *
     * @param msg the error message
     */
    default void showError(final String msg) {
        Notification notification = new Notification();
        notification.setText(msg);
        notification.setPosition(Position.MIDDLE);
        notification.setDuration(1500);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.open();
    }

}
