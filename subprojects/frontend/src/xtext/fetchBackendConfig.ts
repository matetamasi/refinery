/*
 * SPDX-FileCopyrightText: 2021-2025 The Refinery Authors <https://refinery.tools/>
 *
 * SPDX-License-Identifier: EPL-2.0
 */

import BackendConfig, { ENDPOINT } from './BackendConfig';

export type BackendConfigWithDefaults = {
  [P in keyof BackendConfig]-?: NonNullable<BackendConfig[P]>;
};

export default async function fetchBackendConfig(): Promise<BackendConfigWithDefaults> {
  const configURL = `${import.meta.env.BASE_URL}${ENDPOINT}`;
  const response = await fetch(configURL);
  const rawConfig = (await response.json()) as unknown;
  const parsedConfig = BackendConfig.parse(rawConfig);
  return {
    apiBase: parsedConfig.apiBase ?? `${window.origin}/api/v1`,
    webSocketURL:
      parsedConfig.webSocketURL ??
      `${window.origin.replace(/^http/, 'ws')}/xtext-service`,
  };
}
