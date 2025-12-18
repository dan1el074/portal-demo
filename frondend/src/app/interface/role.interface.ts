export interface Role {
  id:  number;
  authority: string;
  parent: string;
  title: string;
}

export interface GroupedRole {
  title: string;
  content: Array<string>;
}
