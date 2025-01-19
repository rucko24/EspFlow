import { unsafeCSS, registerStyles } from '@vaadin/vaadin-themable-mixin/register-styles';

import vaadinUploadFileCss from 'themes/espflow/components/vaadin-upload-file.css?inline';
import vaadinTabsheetCss from 'themes/espflow/components/vaadin-tabsheet.css?inline';
import vaadinCheckboxCss from 'themes/espflow/components/vaadin-checkbox.css?inline';
import vaadinAppLayoutCss from 'themes/espflow/components/vaadin-app-layout.css?inline';
import stepperCss from 'themes/espflow/components/stepper.css?inline';
import listCss from 'themes/espflow/components/list.css?inline';
import layoutCss from 'themes/espflow/components/layout.css?inline';
import inputFieldCss from 'themes/espflow/components/input-field.css?inline';
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
        'vaadin-checkbox',
        unsafeCSS(vaadinCheckboxCss.toString())
      );
      registerStyles(
        'vaadin-app-layout',
        unsafeCSS(vaadinAppLayoutCss.toString())
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
        'input-field',
        unsafeCSS(inputFieldCss.toString())
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

