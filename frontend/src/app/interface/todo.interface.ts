export interface TodoTableCard {
  priority: string;
  items: TodoTableItem[];
}

export interface TodoTableItem {
  title: string;
  date: Date | string;
  late: boolean;
}
