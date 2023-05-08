export const interruptInputEndpoint = "0x81" as const;
export const bulkInputEndpoint = "0x82" as const;
export const bulkOutputEndpoint = "0x02" as const;

export type InterruptInputEndpoint = typeof interruptInputEndpoint;
export type BulkInputEndpoint = typeof bulkInputEndpoint;
export type BulkOutputEndpoint = typeof bulkOutputEndpoint;
