import { unsafeCSS, registerStyles } from '@vaadin/vaadin-themable-mixin/register-styles';

import vaadinUploadFileCss from 'themes/espflow/components/vaadin-upload-file.css?inline';
import vaadinTabsheetCss from 'themes/espflow/components/vaadin-tabsheet.css?inline';
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
        'vaadin-tabsheet',
        unsafeCSS(vaadinTabsheetCss.toString())
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

