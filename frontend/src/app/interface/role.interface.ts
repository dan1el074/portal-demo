export interface Role {
  id:  number;
  authority: string;
  parent: string;
  parentUrl: string;
  title: string;
  titleUrl: string;
}

export interface GroupedRole {
  title: string;
  content: Array<string>;
}
