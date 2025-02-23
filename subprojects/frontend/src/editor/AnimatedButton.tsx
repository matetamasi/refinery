/*
 * SPDX-FileCopyrightText: 2021-2023 The Refinery Authors <https://refinery.tools/>
 *
 * SPDX-License-Identifier: EPL-2.0
 */

import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import { styled, type SxProps, type Theme } from '@mui/material/styles';
import React, { type ReactNode, useLayoutEffect, useState } from 'react';

const AnimatedButtonBase = styled(Button, {
  shouldForwardProp: (prop) => prop !== 'width',
})<{ width: string }>(({ theme, width }) => {
  // Transition copied from `@mui/material/Button`.
  const colorTransition = theme.transitions.create(
    ['background-color', 'box-shadow', 'border-color', 'color'],
    {
      duration: theme.transitions.duration.short,
    },
  );
  return {
    width,
    // Make sure the button does not change width if a number is updated.
    fontVariantNumeric: 'tabular-nums',
    transition: `
      ${colorTransition},
      ${theme.transitions.create(['width'], {
        duration: theme.transitions.duration.short,
      })}
    `,
    '@media (prefers-reduced-motion: reduce)': {
      transition: colorTransition,
    },
  };
});

export default function AnimatedButton({
  'aria-label': ariaLabel,
  role: ariaRole,
  'aria-checked': ariaChecked,
  onClick,
  color,
  disabled,
  startIcon,
  sx,
  children,
}: {
  'aria-label'?: string;
  role?: string;
  'aria-checked'?: boolean;
  className?: string;
  onClick?: React.MouseEventHandler<HTMLElement>;
  color: 'error' | 'warning' | 'primary' | 'inherit' | 'dim';
  disabled?: boolean;
  startIcon?: React.ReactElement;
  sx?: SxProps<Theme> | undefined;
  children?: ReactNode;
}): React.ReactElement {
  const [width, setWidth] = useState<string | undefined>();
  const [contentsElement, setContentsElement] = useState<HTMLDivElement | null>(
    null,
  );

  useLayoutEffect(() => {
    if (contentsElement !== null) {
      const updateWidth = () => {
        setWidth(window.getComputedStyle(contentsElement).width);
      };
      updateWidth();
      const observer = new ResizeObserver(updateWidth);
      observer.observe(contentsElement);
      return () => observer.unobserve(contentsElement);
    }
    return undefined;
  }, [setWidth, contentsElement]);

  return (
    <AnimatedButtonBase
      {...(ariaLabel === undefined ? {} : { 'aria-label': ariaLabel })}
      {...(ariaRole === undefined ? {} : { role: ariaRole })}
      {...(ariaChecked === undefined ? {} : { 'aria-checked': ariaChecked })}
      {...(onClick === undefined ? {} : { onClick })}
      {...(sx === undefined ? {} : { sx })}
      color={color === 'dim' ? 'inherit' : color}
      className={`rounded ${color === 'dim' ? ' shaded-dim' : 'shaded'}`}
      disabled={disabled ?? false}
      startIcon={startIcon}
      width={
        width === undefined
          ? 'auto'
          : `calc(${width} + ${startIcon === undefined ? 28 : 50}px)`
      }
    >
      <Box
        display="flex"
        flexDirection="row"
        justifyContent="end"
        overflow="hidden"
        width="100%"
      >
        <Box whiteSpace="nowrap" ref={setContentsElement}>
          {children}
        </Box>
      </Box>
    </AnimatedButtonBase>
  );
}
