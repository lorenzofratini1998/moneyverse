export interface GaugeData {
  value: number;
  name?: string;
}

export interface BarChartOptions {
  labels: string[];
  series: {
    name: string;
    data: number[];
  }[];
}

export type Orientation = 'horizontal' | 'vertical';

export interface LineChartOptions {
  labels?: string[];
  series: [{
    name: string;
    data: number[];
  }]
}

export interface LineChartConfig {
  showAverage?: boolean
  showArea?: boolean
  showLegend?: boolean
  smooth?: boolean
  tooltipFormatter?: (params: any[]) => string
  yAxisFormatter?: (value: any) => string | null
}

export interface PieChartOptions {
  data: {
    name: string;
    value: number;
  }[];
}
