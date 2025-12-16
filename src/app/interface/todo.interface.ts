export interface TodoCard {
  title: string;
  date: string;
  late: boolean;
}

export interface TodoTableCard {
  priority: string;
  items: Array<TodoCard>;
}
