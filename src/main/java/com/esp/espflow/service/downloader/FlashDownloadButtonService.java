package com.esp.espflow.service.downloader;

import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.stereotype.Service;

/**
 * @author rubn
 */
@Service
@UIScope
public class FlashDownloadButtonService {

    /**
     * It is necessary to instantiate this button for each Slide, so that it is personalized with its url from the tmp directory.
     *
     * @return FlashDownloadButtonWrapper
     */
    public FlashDownloadButtonWrapper getFlashDownloadButtonWrapper() {
        return new FlashDownloadButtonWrapper();
    }

}
