package com.nodemcuui.tool.views.readflash;

import com.nodemcuui.tool.data.entity.EspDeviceInfo;
import com.nodemcuui.tool.data.util.downloader.FlashButtonWrapper;
import com.vaadin.flow.component.button.Button;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 * @author rubn
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class DeviceFactoryCardLayout {

    /**
     * DeviceCardLayout div
     *
     * @param image
     * @param espDeviceInfo
     * @param downloadFlash
     * @param flashButtonWrapper
     *
     * @return a {@link DeviceCardLayout}
     */
    public static DeviceCardLayout createDeviceCard(final String image, final EspDeviceInfo espDeviceInfo,
                                                    final Button downloadFlash, final FlashButtonWrapper flashButtonWrapper) {
        return new DeviceCardLayout(image, espDeviceInfo, downloadFlash, flashButtonWrapper);
    }

}
