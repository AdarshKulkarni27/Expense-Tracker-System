import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import Budget from "./Budget";
import { fetchBudgetPageData, getApiErrorMessage } from "../services/api";

jest.mock("../components/Navbar", () => () => <div>Navbar</div>);

jest.mock("../services/api", () => ({
  createBudget: jest.fn(),
  deleteBudget: jest.fn(),
  fetchBudgetPageData: jest.fn(),
  getApiErrorMessage: jest.fn(() => "Mock API error"),
  updateBudget: jest.fn(),
}));

jest.mock("../utils/auth", () => ({
  getCurrentUserId: jest.fn(() => 1),
}));

describe("Budget history mode", () => {
  beforeEach(() => {
    jest.clearAllMocks();
    jest.useFakeTimers();
    jest.setSystemTime(new Date("2026-05-15T10:00:00Z"));

    fetchBudgetPageData
      .mockResolvedValueOnce({
        budgets: [],
        categories: [{ id: 1, name: "Food" }],
      })
      .mockResolvedValueOnce({
        budgets: [
          {
            id: 11,
            categoryId: 1,
            categoryName: "Food",
            monthlyLimit: 5000,
            spent: 3200,
            month: "2026-04",
          },
        ],
        categories: [{ id: 1, name: "Food" }],
      });

    getApiErrorMessage.mockReturnValue("Mock API error");
  });

  afterEach(() => {
    jest.useRealTimers();
  });

  test("shows past budgets in read-only mode", async () => {
    render(<Budget currentPath="/budget" onNavigate={jest.fn()} />);

    await waitFor(() =>
      expect(fetchBudgetPageData.mock.calls.map(([, requestedMonth]) => requestedMonth)).toEqual([
        "2026-05",
      ])
    );

    fireEvent.change(screen.getByLabelText(/month/i), {
      target: { value: "2026-04" },
    });

    await waitFor(() =>
      expect(fetchBudgetPageData.mock.calls.map(([, requestedMonth]) => requestedMonth)).toEqual([
        "2026-05",
        "2026-04",
      ])
    );

    await screen.findByText("Read only");

    expect(screen.getByRole("button", { name: /\+ set budget/i })).toBeDisabled();
    expect(screen.queryByRole("button", { name: /edit/i })).not.toBeInTheDocument();
    expect(screen.queryByRole("button", { name: /delete/i })).not.toBeInTheDocument();
    expect(screen.getByText("Food")).toBeInTheDocument();
  });
});
