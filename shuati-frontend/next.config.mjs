/** @type {import('next').NextConfig} */
const nextConfig = {
    output: "standalone",
    typescript: {
        // !! WARN !!
        // Dangerously allow production builds to successfully complete even if
        // your project has type errors.
        // !! WARN !!
        ignoreBuildErrors: true,
    },
    async rewrites() {
        const apiTarget =
            process.env.INTERNAL_API_BASE_URL ||
            process.env.NEXT_PUBLIC_API_BASE_URL ||
            "http://127.0.0.1:8101";

        return [
            {
                source: "/api/:path*",
                destination: `${apiTarget}/api/:path*`,
            },
        ];
    },
};

export default nextConfig;
