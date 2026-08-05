from flask_bcrypt import generate_password_hash
from database import db
from models.user_model import User
from flask_jwt_extended import create_access_token
from flask_bcrypt import generate_password_hash, check_password_hash
def register_user(name, email, password, role):

    existing_user = User.query.filter_by(email=email).first()

    if existing_user:
        return {
            "success": False,
            "message": "Email already exists"
        }

    hashed_password = generate_password_hash(password).decode("utf-8")

    user = User(
        name=name,
        email=email,
        password=hashed_password,
        role=role
    )

    db.session.add(user)
    db.session.commit()

    return {
        "success": True,
        "message": "User Registered Successfully"
    }


def login_user(email, password):

    user = User.query.filter_by(email=email).first()

    if not user:
        return {
            "success": False,
            "message": "Invalid Email"
        }

    if not check_password_hash(user.password, password):
        return {
            "success": False,
            "message": "Invalid Password"
        }
    access_token = create_access_token(
            identity=str(user.id),
            additional_claims={
                "role": user.role
            }
        )
    return {
    "success": True,
    "message": "Login Successful",
    "access_token": access_token,
    "user": {
        "id": user.id,
        "name": user.name,
        "email": user.email,
        "role": user.role
    }
}