# Customer Manager (ICT261 Lecture 3 classroom lab)

A JavaFX application covering the six lab steps.

| Step | Where |
|------|-------|
| 1. Form with name field and province list | `MainApp.start()` - `TextField` + `ComboBox` |
| 2. `Customer` and `ObservableList<Customer>` | `Customer.java`, `customers` in `MainApp` |
| 3. `TableView` with name and province columns | `MainApp.start()` |
| 4. Validate input, then add | `MainApp.addCustomer()` |
| 5. Confirm deletion of selected customer | `MainApp.deleteSelected()` |
| 6. Test invalid input and keyboard access | See checklist below |

## Run

Requires JDK 17+ and Maven.

```
mvn javafx:run
```

## Test checklist (step 6)

Invalid input:
- Empty name -> "Name is required."
- Name of 1 character -> length error
- Name with digits/symbols (e.g. `J0hn@`) -> character error
- No province selected -> province error
- Same name + province twice -> duplicate error

Keyboard access:
- `Alt+N` focuses Name, `Alt+P` focuses Province, `Alt+A` adds, `Alt+D` deletes
- `Ctrl+N` jumps to the Name field
- `Enter` adds the customer (default button)
- `Tab` / `Shift+Tab` moves through the form, table, and buttons
- Select a row with arrow keys, press `Delete` -> confirmation dialog
