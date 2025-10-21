import js from "@eslint/js";
import eslintConfigPrettier from "eslint-config-prettier";
import pluginReact from "eslint-plugin-react";
import pluginReactHooks from "eslint-plugin-react-hooks";
import pluginReactRefresh from "eslint-plugin-react-refresh";
import globals from "globals";
import tseslint from "typescript-eslint";
import { config as baseConfig } from "./base.js";

/**
 * A custom ESLint configuration for libraries that use React.
 *
 * @type {import("eslint").Linter.Config[]} */
export const viteReactConfig = [
  ...baseConfig,

  // Core JS + TS configs
  js.configs.recommended,
  ...tseslint.configs.recommended,

  // React base config (with JSX settings)
  pluginReact.configs.flat.recommended,

  // React Refresh preset
  pluginReactRefresh.configs.vite,

  // Prettier to disable stylistic conflicts
  eslintConfigPrettier,
  {
    languageOptions: {
      ...pluginReact.configs.flat.recommended.languageOptions,
      globals: {
        ...globals.serviceworker,
        ...globals.browser,
      },
    },
  },
  {
    plugins: {
      "react-hooks": pluginReactHooks,
      "react-refresh": pluginReactRefresh,
    },
    settings: { react: { version: "detect" } },
    rules: {
      ...pluginReactHooks.configs.recommended.rules,

      // React scope no longer necessary with new JSX transform.
      "react/react-in-jsx-scope": "off",

      // Enforce React Refresh safety
      "react-refresh/only-export-components": [
        "warn",
        { allowConstantExport: true },
      ],
    },
  },
];
