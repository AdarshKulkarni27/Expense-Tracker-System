import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import Reports from "./Reports";
import { fetchReportsData, getApiErrorMessage } from "../services/api";

jest.mock("../components/Navbar", () => () => <div>Navbar</div>);

jest.mock("react-chartjs-2", () => ({
  Bar: () => <div>bar chart</div>,
  Pie: () => <div>pie chart</div>,
}));

jest.mock("../services/api", () => ({
  fetchReportsData: jest.fn(),
  getApiErrorMessage: jest.fn(() => "Mock API error"),
}));

jest.mock("../utils/auth", () => ({
  getCurrentUserId: jest.fn(() => 2),
}));

describe("Reports page", () => {
  beforeEach(() => {
    jest.clearAllMocks();
    jest.useFakeTimers();
    jest.setSystemTime(new Date("2026-05-15T10:00:00Z"));

    getApiErrorMessage.mockReturnValue("Mock API error");
  });

  afterEach(() => {
    jest.useRealTimers();
  });

  test("switches the month picker to the latest month with report activity", async () => {
    fetchReportsData
      .mockResolvedValueOnce({
        summary: {
          totalSpending: 6000,
          transactionCount: 2,
          average: 3000,
          highest: 4000,
          categoryCount: 1,
        },
        categoryData: [{ category: "Food", total: 6000, percentage: 100 }],
        trendData: [{ month: "Apr", total: 6000 }],
        selectedMonth: "2026-04",
      })
      .mockResolvedValueOnce({
        summary: {
          totalSpending: 6000,
          transactionCount: 2,
          average: 3000,
          highest: 4000,
          categoryCount: 1,
        },
        categoryData: [{ category: "Food", total: 6000, percentage: 100 }],
        trendData: [{ month: "Apr", total: 6000 }],
        selectedMonth: "2026-04",
      });

    render(<Reports currentPath="/reports" onNavigate={jest.fn()} />);

    await waitFor(() =>
      expect(fetchReportsData).toHaveBeenCalledTimes(2)
    );

    await waitFor(() =>
      expect(
        fetchReportsData.mock.calls.map(([, month, options]) => ({
          month,
          allowMonthFallback: options?.allowMonthFallback,
        }))
      ).toEqual([
        { month: "2026-05", allowMonthFallback: true },
        { month: "2026-04", allowMonthFallback: false },
      ])
    );

    await waitFor(() =>
      expect(screen.getByRole("combobox")).toHaveValue("2026-04")
    );

    expect(screen.getByText("2 transactions")).toBeInTheDocument();
    expect(screen.getAllByText("Food").length).toBeGreaterThan(0);
  });

  test("keeps a manually selected month even when it has no data", async () => {
    fetchReportsData
      .mockResolvedValueOnce({
        summary: {
          totalSpending: 6000,
          transactionCount: 2,
          average: 3000,
          highest: 4000,
          categoryCount: 1,
        },
        categoryData: [{ category: "Food", total: 6000, percentage: 100 }],
        trendData: [{ month: "Apr", total: 6000 }],
        selectedMonth: "2026-04",
      })
      .mockResolvedValueOnce({
        summary: {
          totalSpending: 6000,
          transactionCount: 2,
          average: 3000,
          highest: 4000,
          categoryCount: 1,
        },
        categoryData: [{ category: "Food", total: 6000, percentage: 100 }],
        trendData: [{ month: "Apr", total: 6000 }],
        selectedMonth: "2026-04",
      })
      .mockResolvedValueOnce({
        summary: {
          totalSpending: 0,
          transactionCount: 0,
          average: 0,
          highest: 0,
          categoryCount: 0,
        },
        categoryData: [],
        trendData: [{ month: "Apr", total: 6000 }],
        selectedMonth: "2026-03",
      });

    render(<Reports currentPath="/reports" onNavigate={jest.fn()} />);

    await waitFor(() =>
      expect(screen.getByRole("combobox")).toHaveValue("2026-04")
    );

    fireEvent.change(screen.getByRole("combobox"), {
      target: { value: "2026-03" },
    });

    await waitFor(() =>
      expect(
        fetchReportsData.mock.calls.map(([, month, options]) => ({
          month,
          allowMonthFallback: options?.allowMonthFallback,
        }))
      ).toEqual([
        { month: "2026-05", allowMonthFallback: true },
        { month: "2026-04", allowMonthFallback: false },
        { month: "2026-03", allowMonthFallback: false },
      ])
    );

    await waitFor(() =>
      expect(screen.getByRole("combobox")).toHaveValue("2026-03")
    );

    await waitFor(() =>
      expect(screen.getByText("No category data")).toBeInTheDocument()
    );

    expect(screen.getByText("0 transactions")).toBeInTheDocument();
  });
});
