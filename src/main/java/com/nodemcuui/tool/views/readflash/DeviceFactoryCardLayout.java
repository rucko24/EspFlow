package com.nodemcuui.tool.views.readflash;

import com.nodemcuui.tool.data.entity.EspDeviceInfo;
import com.vaadin.flow.component.button.Button;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.vaadin.firitin.components.DynamicFileDownloader;

/**
 * @author rubn
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class DeviceFactoryCardLayout {

    public static DeviceCardLayout createDeviceCard(final String image, final EspDeviceInfo espDeviceInfo, final Button downloadFlash) {
        return new DeviceCardLayout(image, espDeviceInfo, downloadFlash);
    }

    public static DeviceCardLayout createDeviceCard(final String image, final EspDeviceInfo espDeviceInfo, final DynamicFileDownloader downloadFlash) {
        return new DeviceCardLayout(image, espDeviceInfo, downloadFlash);
    }

}
