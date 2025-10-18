import { Button } from "@chakra-ui/react";

function App() {
  return (
    <>
      <h1>Welcome Customer!</h1>
      <Button onClick={() => alert("Clicked!")}>Click Me</Button>
    </>
  );
}

export default App;
