export interface Role {
  id:  number;
  authority: string;
  parent: string;
  parentUrl: string;
  title: string;
  titleUrl: string;
  activated: boolean;
}

export interface GroupedRole {
  title: string;
  content: Array<string>;
}

export interface RoleSummary {
  id: number;
  authority: string;
  activated: boolean;
  childrens: Array<RoleSummary>;
}

export interface RoleGroup {
  title: string;
  childrens: Array<RoleSummary>;
}
