package com.nodemcuui.tool.views.readflash;

import com.nodemcuui.tool.data.entity.EspDeviceInfo;
import com.nodemcuui.tool.data.util.downloader.DownloadFlashButton;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 * @author rubn
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class DeviceFactoryCardLayout {

    public static DeviceCardLayout createDeviceCard(final String image) {
        return new DeviceCardLayout(image);
    }

    public static DeviceCardLayout createDeviceCard(final String image, final Span chipTypeValue) {
        return new DeviceCardLayout(image, chipTypeValue);
    }

    public static DeviceCardLayout createDeviceCard(final String image, final EspDeviceInfo espDeviceInfo) {
        return new DeviceCardLayout(image, espDeviceInfo);
    }

    public static DeviceCardLayout createDeviceCard(final String image, final EspDeviceInfo espDeviceInfo, final DownloadFlashButton downloadFlash) {
        return new DeviceCardLayout(image, espDeviceInfo, downloadFlash);
    }

}
