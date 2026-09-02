"use client";

import { useState } from "react";

interface QuantityStepperProps {
  name?: string;
  min?: number;
  max?: number;
  defaultValue?: number;
}

/**
 * Client island: −/+ buttons around a real `<input type="number">`. The
 * input alone is a functional form control even before hydration, so this
 * degrades gracefully (buttons just don't respond yet).
 */
export function QuantityStepper({ name = "quantity", min = 1, max = 99, defaultValue = 1 }: QuantityStepperProps) {
  const [value, setValue] = useState(defaultValue);

  function clamp(next: number): number {
    if (Number.isNaN(next)) return min;
    return Math.min(max, Math.max(min, Math.trunc(next)));
  }

  return (
    <div className="quantity-stepper">
      <button type="button" aria-label="Giảm số lượng" onClick={() => setValue((v) => clamp(v - 1))}>
        −
      </button>
      <input
        type="number"
        name={name}
        min={min}
        max={max}
        inputMode="numeric"
        value={value}
        onChange={(event) => setValue(clamp(Number(event.target.value)))}
        aria-label="Số lượng"
      />
      <button type="button" aria-label="Tăng số lượng" onClick={() => setValue((v) => clamp(v + 1))}>
        +
      </button>
    </div>
  );
}
