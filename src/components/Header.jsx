import { Link } from "react-router-dom";

function Header() {
    return (
        <header
            style={{
                background: "#111",
                color: "white",
                padding: "15px 30px",
                display: "flex",
                justifyContent: "space-between",
                alignItems: "center"
            }}
        >
            <h2>SPORT PRO</h2>

            <nav>
                <Link to="/" style={{ color: "white", marginRight: "20px" }}>
                    Trang chủ
                </Link>

                <Link to="/products" style={{ color: "white", marginRight: "20px" }}>
                    Sản phẩm
                </Link>

                <Link to="/cart" style={{ color: "white", marginRight: "20px" }}>
                    Giỏ hàng
                </Link>

                <Link to="/login" style={{ color: "white" }}>
                    Đăng nhập
                </Link>
            </nav>
        </header>
    );
}

export default Header;