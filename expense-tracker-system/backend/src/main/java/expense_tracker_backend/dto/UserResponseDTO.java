package expense_tracker_backend.dto;

public class UserResponseDTO {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private Boolean overBudget;
    private String overBudgetAlertMessage;

    public UserResponseDTO(Long id, String name, String email, String phone) {
        this(id, name, email, phone, false, null);
    }

    public UserResponseDTO(
            Long id,
            String name,
            String email,
            String phone,
            Boolean overBudget,
            String overBudgetAlertMessage
    ) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.overBudget = overBudget;
        this.overBudgetAlertMessage = overBudgetAlertMessage;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public Boolean getOverBudget() { return overBudget; }
    public String getOverBudgetAlertMessage() { return overBudgetAlertMessage; }
}
