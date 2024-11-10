import { unsafeCSS, registerStyles } from '@vaadin/vaadin-themable-mixin/register-styles';

import vaadinUploadFileCss from 'themes/espflow/components/vaadin-upload-file.css?inline';
import vaadinTextAreaCss from 'themes/espflow/components/vaadin-text-area.css?inline';
import vaadinInputContainerCss from 'themes/espflow/components/vaadin-input-container.css?inline';
import stepperCss from 'themes/espflow/components/stepper.css?inline';
import listCss from 'themes/espflow/components/list.css?inline';
import layoutCss from 'themes/espflow/components/layout.css?inline';
import borderCss from 'themes/espflow/components/border.css?inline';


if (!document['_vaadintheme_espflow_componentCss']) {
  registerStyles(
        'vaadin-upload-file',
        unsafeCSS(vaadinUploadFileCss.toString())
      );
      registerStyles(
        'vaadin-text-area',
        unsafeCSS(vaadinTextAreaCss.toString())
      );
      registerStyles(
        'vaadin-input-container',
        unsafeCSS(vaadinInputContainerCss.toString())
      );
      registerStyles(
        'stepper',
        unsafeCSS(stepperCss.toString())
      );
      registerStyles(
        'list',
        unsafeCSS(listCss.toString())
      );
      registerStyles(
        'layout',
        unsafeCSS(layoutCss.toString())
      );
      registerStyles(
        'border',
        unsafeCSS(borderCss.toString())
      );
      
  document['_vaadintheme_espflow_componentCss'] = true;
}

if (import.meta.hot) {
  import.meta.hot.accept((module) => {
    window.location.reload();
  });
}

