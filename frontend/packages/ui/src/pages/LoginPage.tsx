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
import { Role } from "@repo/shared/models";
import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { Link, useNavigate } from "react-router-dom";
import { LoadingSpinner } from "../components";
import { PasswordInput } from "../components/ui/password-input";
import { ErrorPage } from "./ErrorPage";

interface FormValues {
  email: string;
  password: string;
}

interface LoginContent {
  title: string;
  description: string;
}

const LOGIN_CONTENT_BY_ROLE: Record<Role, LoginContent> = {
  [Role.CUSTOMER]: {
    title: "Welcome Back",
    description: "Sign in to continue",
  },
  [Role.RESTAURANT_ADMIN]: {
    title: "Restaurant Login",
    description: "Manage your restaurant operations",
  },
  [Role.DRIVER]: {
    title: "Driver Login",
    description: "Access your delivery routes",
  },
  [Role.SYSTEM_ADMIN]: {
    title: "Admin Login",
    description: "Manage the system",
  },
};

const SERVER_ERROR_MESSAGE = "Server error, please try again later";

export const LoginPage = ({ role }: { role: Role }) => {
  const navigate = useNavigate();
  const { user, isLoading, isInitialized, login, initialize } = useAuthStore();
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<FormValues>();
  const [error, setError] = useState<string | null>(null);

  const onSubmit = async (data: FormValues) => {
    try {
      await login(data.email, data.password, role);
      // With per-app basename, navigating to root goes to the correct app dashboard
      navigate("/");
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || error.message;

      console.error("Login failed:", errorMessage);
      setError(errorMessage);
    }
  };

  useEffect(() => {
    if (!isInitialized) initialize();
    if (user) navigate("/");
  }, [initialize, user]);

  if (isLoading || !isInitialized) {
    return <LoadingSpinner />;
  }

  return (
    <>
      {!isLoading && isInitialized ? (
        <AbsoluteCenter>
          <form onSubmit={handleSubmit(onSubmit)}>
            <Card.Root
              width={{ base: "100%", lg: "400px" }}
              overflow="auto"
              shadow="md"
              rounded="md"
            >
              <Card.Header>
                <Card.Title>{LOGIN_CONTENT_BY_ROLE[role].title}</Card.Title>
                <Card.Description>
                  {LOGIN_CONTENT_BY_ROLE[role].description}
                </Card.Description>
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
                      <Alert.Content>{error}</Alert.Content>
                    </Alert.Root>
                  )}
                </Stack>
              </Card.Body>
              <Card.Footer justifyContent="center">
                {role !== Role.SYSTEM_ADMIN && (
                  <>
                    <Text>Don't have an account?</Text>
                    <ChakraLink asChild>
                      <Link to="/signup" replace>
                        <b>Sign up</b>
                      </Link>
                    </ChakraLink>
                  </>
                )}
              </Card.Footer>
            </Card.Root>
          </form>
        </AbsoluteCenter>
      ) : (
        <ErrorPage errorMessage={SERVER_ERROR_MESSAGE} />
      )}
    </>
  );
};
