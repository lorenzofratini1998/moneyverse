interface ColorOption {
  name: string;
  light: {
    background: string;
    text: string;
  };
  dark?: {
    background: string;
    text: string;
  };
  selected: boolean;
}

export interface Color {
  name: string;
  background: string;
  text: string;
  selected: boolean;
}

export const COLORS: ColorOption[] = [
  {
    name: 'red',
    light: {
      background: '#FEE2E2',
      text: '#EF4444'
    },
    dark: {
      background: '#7F1D1D',
      text: '#FECACA'
    },
    selected: true
  },
  {
    name: 'orange',
    light: {
      background: '#FFEDD5',
      text: '#F97316'
    },
    dark: {
      background: '#7C2D12',
      text: '#FED7AA'
    },
    selected: false
  },
  {
    name: 'amber',
    light: {
      background: '#FEF3C7',
      text: '#F59E0B'
    },
    dark: {
      background: '#78350F',
      text: '#FDE68A'
    },
    selected: false
  },
  {
    name: 'yellow',
    light: {
      background: '#FEF9C3',
      text: '#EAB308'
    },
    dark: {
      background: '#713F12',
      text: '#FEF08A'
    },
    selected: false
  },
  {
    name: 'lime',
    light: {
      background: '#ECFCCB',
      text: '#84CC16'
    },
    dark: {
      background: '#365314',
      text: '#D9F99D'
    },
    selected: false
  },
  {
    name: 'green',
    light: {
      background: '#DCFCE7',
      text: '#22C55E'
    },
    dark: {
      background: '#14532D',
      text: '#BBF7D0'
    },
    selected: false
  },
  {
    name: 'emerald',
    light: {
      background: '#D1FAE5',
      text: '#10B981'
    },
    dark: {
      background: '#064E3B',
      text: '#A7F3D0'
    },
    selected: false
  },
  {
    name: 'teal',
    light: {
      background: '#CCFBF1',
      text: '#14B8A6'
    },
    dark: {
      background: '#134E4A',
      text: '#99F6E4'
    },
    selected: false
  },
  {
    name: 'cyan',
    light: {
      background: '#CFFAFE',
      text: '#06B6D4'
    },
    dark: {
      background: '#164E63',
      text: '#A5F3FC'
    },
    selected: false
  },
  {
    name: 'sky',
    light: {
      background: '#E0F2FE',
      text: '#0EA5E9'
    },
    dark: {
      background: '#0C4A6E',
      text: '#BAE6FD'
    },
    selected: false
  },
  {
    name: 'blue',
    light: {
      background: '#DBEAFE',
      text: '#3B82F6'
    },
    dark: {
      background: '#1E40AF',
      text: '#BFDBFE'
    },
    selected: false
  },
  {
    name: 'indigo',
    light: {
      background: '#E0E7FF',
      text: '#6366F1'
    },
    dark: {
      background: '#3730A3',
      text: '#C7D2FE'
    },
    selected: false
  },
  {
    name: 'violet',
    light: {
      background: '#EEE1FF',
      text: '#8B5CF6'
    },
    dark: {
      background: '#5B21B6',
      text: '#DDD6FE'
    },
    selected: false
  },
  {
    name: 'purple',
    light: {
      background: '#F3E8FF',
      text: '#A855F7'
    },
    dark: {
      background: '#6B21A8',
      text: '#E9D5FF'
    },
    selected: false
  },
  {
    name: 'fuchsia',
    light: {
      background: '#FDF4FF',
      text: '#D946EF'
    },
    dark: {
      background: '#86198F',
      text: '#F5D0FE'
    },
    selected: false
  },
  {
    name: 'pink',
    light: {
      background: '#FDF2F8',
      text: '#EC4899'
    },
    dark: {
      background: '#831843',
      text: '#FBCFE8'
    },
    selected: false
  },
  {
    name: 'rose',
    light: {
      background: '#FFE4E6',
      text: '#F43F5E'
    },
    dark: {
      background: '#9F1239',
      text: '#FECDD3'
    },
    selected: false
  },
  /* Color neutri (scommenta se necessario)
  {
    name: 'slate',
    light: {
      background: '#F1F5F9',
      text: '#64748B'
    },
    dark: {
      background: '#1E293B',
      text: '#E2E8F0'
    },
    selected: false
  },
  {
    name: 'gray',
    light: {
      background: '#F3F4F6',
      text: '#6B7280'
    },
    dark: {
      background: '#1F2937',
      text: '#E5E7EB'
    },
    selected: false
  },
  {
    name: 'zinc',
    light: {
      background: '#F4F4F5',
      text: '#71717A'
    },
    dark: {
      background: '#18181B',
      text: '#E4E4E7'
    },
    selected: false
  },
  {
    name: 'neutral',
    light: {
      background: '#F5F5F5',
      text: '#737373'
    },
    dark: {
      background: '#171717',
      text: '#E5E5E5'
    },
    selected: false
  },
  {
    name: 'stone',
    light: {
      background: '#F5F5F4',
      text: '#78716C'
    },
    dark: {
      background: '#1C1917',
      text: '#E7E5E4'
    },
    selected: false
  }
  */
];
