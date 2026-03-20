package ui.pages;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class AdminPage {
    private final SelenideElement listUsersTable = $("#list-users-table");

    public AdminPage open() {
        Selenide.open("/admin/users");
        return this;
    }

    public boolean isUserPresent(String userName) {
        return listUsersTable.$$("tr").stream()
                .anyMatch(row -> row.getText().contains(userName));
    }
}
