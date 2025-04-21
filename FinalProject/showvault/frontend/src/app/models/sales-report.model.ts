export interface SalesReport {
    totalRevenue: number;
    ticketsSold: number;
    averageTicketPrice: number;
    conversionRate: number;
    revenueTrend: 'increasing' | 'decreasing' | 'stable';
    revenueByShow: Array<{
        showTitle: string;
        revenue: number;
    }>;
    revenueByCategory: Array<{
        category: string;
        revenue: number;
    }>;
    revenueByMonth: Array<{
        month: string;
        amount: number;
    }>;
    maxMonthlyRevenue: number;
    revenueByPaymentMethod: Array<{
        method: string;
        revenue: number;
    }>;
    topSellingShows: Array<{
        name: string;
        organizer: string;
        ticketsSold: number;
        revenue: number;
        averagePrice: number;
    }>;
}