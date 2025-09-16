import { createBrowserRouter } from "react-router";
import App from "./App";

const AppRoutes = createBrowserRouter([
    {
        path: "/",
        Component: App
    }
]);

export default AppRoutes;