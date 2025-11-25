import { AbsoluteCenter, HStack, Spinner, Text } from "@chakra-ui/react";
import { ReactNode } from "react";

export const LoadingSpinner = ({
  text = "Loading...",
  children,
}: {
  text?: string;
  children?: ReactNode;
}) => {
  return (
    <AbsoluteCenter>
      <HStack>
        <Spinner />
        {text && <Text>{text}</Text>}
        {children}
      </HStack>
    </AbsoluteCenter>
  );
};
