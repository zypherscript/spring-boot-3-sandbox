package com.example.demo.vaadin;

import com.example.demo.entity.Customer;
import com.example.demo.repository.CustomerRepository;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import java.util.stream.StreamSupport;

@RolesAllowed("ADMIN")
@Route(value = "", layout = MainLayout.class)
@PageTitle("Customers")
class CustomerView extends AbstractAdminView {

  final Grid<Customer> grid;

  private final CustomerRepository customerRepository;

  public CustomerView(CustomerRepository customerRepository) {
    this.customerRepository = customerRepository;

    grid = new Grid<>();
    grid.addColumn(Customer::id).setHeader("ID");
    grid.addColumn(Customer::name).setHeader("Name");

    add(grid);

    listCustomer();
  }

  private void listCustomer() {
    var customers = StreamSupport.stream(customerRepository.findAll()
        .spliterator(), false).toList();
    grid.setItems(customers);
  }
}
