package com.example.demo.vaadin;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import jakarta.annotation.security.RolesAllowed;

@RolesAllowed("ADMIN")
public abstract class AbstractAdminView extends VerticalLayout {

}
