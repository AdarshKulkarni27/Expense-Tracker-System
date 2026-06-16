import { render, waitFor } from "@testing-library/react";
import Dashboard from "./Dashboard";
import {
  fetchBudgetPageData,
  fetchDashboardData,
  getApiErrorMessage,
} from "../services/api";

jest.mock("../components/Navbar", () => () => <div>Navbar</div>);
jest.mock("../components/Cards", () => ({
  SummaryCards: () => <div>SummaryCards</div>,
  TopExpensesSection: () => <div>TopExpensesSection</div>,
}));
jest.mock("../components/Charts", () => () => <div>Charts</div>);

jest.mock("../services/api", () => ({
  fetchBudgetPageData: jest.fn(),
  fetchDashboardData: jest.fn(),
  getApiErrorMessage: jest.fn(() => "Mock API error"),
}));

jest.mock("../utils/auth", () => ({
  getCurrentUserId: jest.fn(() => 4),
}));

describe("Dashboard budget alerts", () => {
  beforeEach(() => {
    jest.clearAllMocks();
    jest.useFakeTimers();
    jest.setSystemTime(new Date("2026-05-15T10:00:00Z"));
    fetchDashboardData.mockResolvedValue({
      dashboard: {},
      categorySummary: [],
      expenses: [],
      categories: [],
      topExpenses: [],
    });
    getApiErrorMessage.mockReturnValue("Mock API error");
  });

  afterEach(() => {
    jest.useRealTimers();
  });

  test("shows an alert once after login when the user is over budget", async () => {
    const alertSpy = jest.spyOn(window, "alert").mockImplementation(() => {});
    const handleBudgetAlertHandled = jest.fn();

    fetchBudgetPageData.mockResolvedValue({
      budgets: [
        {
          id: 1,
          categoryName: "Food",
          monthlyLimit: 3000,
          spent: 3700,
        },
        {
          id: 2,
          categoryName: "Travel",
          monthlyLimit: 2000,
          spent: 1800,
        },
      ],
      categories: [],
    });

    render(
      <Dashboard
        currentPath="/"
        onNavigate={jest.fn()}
        authSession={{ userId: 4 }}
        onLogout={jest.fn()}
        shouldShowBudgetAlert
        onBudgetAlertHandled={handleBudgetAlertHandled}
      />
    );

    await waitFor(() =>
      expect(
        fetchBudgetPageData.mock.calls.map(([, requestedMonth]) => requestedMonth)
      ).toEqual(["2026-05"])
    );

    await waitFor(() =>
      expect(alertSpy).toHaveBeenCalledWith(
        "Budget alert: You have exceeded your budget for Food by ₹700.00."
      )
    );

    expect(handleBudgetAlertHandled).toHaveBeenCalledTimes(1);

    alertSpy.mockRestore();
  });
});
