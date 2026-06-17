function Home() {
    return (
        <>
            <section
                style={{
                    background: "#e5e5e5",
                    padding: "80px",
                    textAlign: "center"
                }}
            >
                <h1>SPORT PRO</h1>

                <h3>Website bán quần áo thể thao</h3>

                <p>
                    Chuyên cung cấp quần áo thể thao chất lượng cao
                </p>
            </section>

            <section style={{ marginTop: "30px" }}>
                <h2>Sản phẩm nổi bật</h2>

                <div
                    style={{
                        display: "flex",
                        gap: "20px"
                    }}
                >
                    <div style={{ border: "1px solid #ccc", padding: "20px" }}>
                        Áo thể thao
                    </div>

                    <div style={{ border: "1px solid #ccc", padding: "20px" }}>
                        Quần thể thao
                    </div>

                    <div style={{ border: "1px solid #ccc", padding: "20px" }}>
                        Bộ thể thao
                    </div>
                </div>
            </section>
        </>
    );
}

export default Home;