import {
  AbsoluteCenter,
  Alert,
  Button,
  Card,
  Link as ChakraLink,
  Field,
  Input,
  Stack,
  Text,
} from "@chakra-ui/react";
import { useAuthStore } from "@repo/shared/hooks";
import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { Link, useNavigate } from "react-router-dom";
import { PasswordInput } from "../components/ui/password-input";

interface FormValues {
  email: string;
  password: string;
}

export const LoginPage = () => {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<FormValues>();
  const { user, isLoading, isInitialized, login, initialize } = useAuthStore();
  const navigate = useNavigate();
  const [error, setError] = useState<string | null>(null);

  const onSubmit = async (data: FormValues) => {
    try {
      await login(data.email, data.password);
      // With per-app basename, navigating to root goes to the correct app dashboard
      navigate("/");
    } catch (error: any) {
      setError(error.response?.data?.message || "Server is down");
    }
  };

  useEffect(() => {
    if (!isInitialized) initialize();
    if (user) navigate("/");
  }, [initialize, user]);

  return (
    <>
      {isInitialized && (
        <AbsoluteCenter>
          <form onSubmit={handleSubmit(onSubmit)}>
            <Card.Root
              width={{ base: "100%", lg: "400px" }}
              overflow="auto"
              shadow="md"
              rounded="md"
            >
              <Card.Header>
                <Card.Title>Sign in</Card.Title>
                <Card.Description>Access your account</Card.Description>
              </Card.Header>
              <Card.Body>
                <Stack gap="4">
                  <Field.Root invalid={!!errors.email}>
                    <Field.Label>Email</Field.Label>
                    <Input
                      {...register("email", { required: true })}
                      placeholder="Email"
                      type="email"
                    />
                    <Field.ErrorText>
                      {errors.email?.type === "required"} Email is required
                    </Field.ErrorText>
                  </Field.Root>

                  <Field.Root invalid={!!errors.password}>
                    <Field.Label>Password</Field.Label>
                    <PasswordInput
                      {...register("password", { required: true })}
                      placeholder="Password"
                    />
                    <Field.ErrorText>
                      {errors.password?.type === "required"} Password is
                      required
                    </Field.ErrorText>
                  </Field.Root>

                  <Button type="submit" variant="solid" loading={isLoading}>
                    Sign in
                  </Button>
                  {error && (
                    <Alert.Root status="error">
                      <Alert.Indicator />
                      <Alert.Content>Error: {error}</Alert.Content>
                    </Alert.Root>
                  )}
                </Stack>
              </Card.Body>
              <Card.Footer justifyContent="center">
                <Text>Don't have an account?</Text>
                <ChakraLink asChild>
                  <Link to="/signup" replace>
                    <b>Sign up</b>
                  </Link>
                </ChakraLink>
              </Card.Footer>
            </Card.Root>
          </form>
        </AbsoluteCenter>
      )}
    </>
  );
};
