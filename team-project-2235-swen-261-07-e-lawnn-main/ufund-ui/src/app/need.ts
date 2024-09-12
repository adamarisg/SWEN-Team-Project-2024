export interface Need {
    id: number;
    title: string;
    description: string;
    cost: number;
    urgency: number; // Has a min value of 1 and max of 10
}
