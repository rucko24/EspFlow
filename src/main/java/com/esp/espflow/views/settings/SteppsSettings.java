package com.esp.espflow.views.settings;

import com.esp.espflow.views.MainLayout;
import com.esp.espflow.views.flashesp.FlashEspView;
import com.esp.espflow.views.steppers.Step;
import com.esp.espflow.views.steppers.Stepper;
import com.esp.espflow.views.wizard.Step1View;
import com.esp.espflow.views.wizard.Step2View;
import com.esp.espflow.views.wizard.Step3View;
import com.esp.espflow.views.wizard.Step4View;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.RoutePrefix;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.BoxSizing;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.FlexDirection;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import com.vaadin.flow.theme.lumo.LumoUtility.Height;
import com.vaadin.flow.theme.lumo.LumoUtility.IconSize;
import com.vaadin.flow.theme.lumo.LumoUtility.MaxWidth;
import com.vaadin.flow.theme.lumo.LumoUtility.Overflow;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import jakarta.annotation.security.RolesAllowed;
import org.vaadin.lineawesome.LineAwesomeIcon;

@PageTitle("Wizard")
@ParentLayout(MainLayout.class)
@RoutePrefix(value = "wizard")
@RolesAllowed("ADMIN")
public class SteppsSettings extends Dialog implements RouterLayout, AfterNavigationObserver {

    private Stepper stepper;
    private Div content;
    private Div footer;
    private RouterLink previous;
    private RouterLink next;

    public SteppsSettings() {
        this.setHeaderTitle("Settings");
        final Button closeButton = new Button(new Icon("lumo", "cross"),
                (event) -> super.close());
        super.getHeader().add(closeButton);
        addClassNames(Display.FLEX, FlexDirection.COLUMN, Height.FULL);
        add(createStepper(), createContent(), createFooter());
    }

    private Stepper createStepper() {
        Step step1 = new Step("Step 1", PasswordView.class);
        Step step2 = new Step("Step 2", NotificationsView.class);
        Step step3 = new Step("Step 3", ContactInformationView.class);
        Step step4 = new Step("Step 4", PublicInformationView.class);

        this.stepper = new Stepper(step1, step2, step3, step4);
        this.stepper.addClassNames(BoxSizing.BORDER, MaxWidth.SCREEN_SMALL, Padding.MEDIUM);
        this.stepper.setOrientation(Stepper.Orientation.HORIZONTAL);
        this.stepper.setSmall(true);
        return this.stepper;
    }

    private Div createContent() {
        this.content = new Div();
        this.content.addClassNames("flex-1", Overflow.AUTO);
        return this.content;
    }

    private Div createFooter() {
        this.previous = new RouterLink("Previous", Step1View.class);
        this.previous.addComponentAsFirst(createSmallIcon(LineAwesomeIcon.ANGLE_LEFT_SOLID));
        this.previous.addClassNames(AlignItems.CENTER, Display.FLEX, Gap.SMALL);

        this.next = new RouterLink("Next", Step2View.class);
        this.next.add(createSmallIcon(LineAwesomeIcon.ANGLE_RIGHT_SOLID));
        this.next.addClassNames(AlignItems.CENTER, Display.FLEX, Gap.SMALL);

        this.footer = new Div(this.previous, this.next);
        this.footer.addClassNames(Display.FLEX, Gap.XLARGE, Padding.Horizontal.LARGE, Padding.Vertical.MEDIUM);
        return this.footer;
    }

    private Component createSmallIcon(LineAwesomeIcon icon) {
        SvgIcon i = icon.create();
        i.addClassNames(IconSize.SMALL);
        return i;
    }

    @Override
    public void showRouterLayoutContent(HasElement content) {
        if (content != null) {
            this.content.removeAll();
            this.content.getElement().appendChild(content.getElement());
        }
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        if (event.getLocation().getPath().contains("wizard/1")) {
            this.previous.getElement().removeAttribute("href");
            this.next.setRoute(Step2View.class);

        } else if (event.getLocation().getPath().contains("wizard/2")) {
            this.previous.setRoute(Step1View.class);
            this.next.setRoute(Step3View.class);

        } else if (event.getLocation().getPath().contains("wizard/3")) {
            this.previous.setRoute(Step2View.class);
            this.next.setRoute(Step4View.class);

        } else if (event.getLocation().getPath().contains("wizard/4")) {
            this.previous.setRoute(Step3View.class);
            this.next.setRoute(FlashEspView.class);
        }
    }
}