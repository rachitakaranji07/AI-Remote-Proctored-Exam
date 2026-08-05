from flask import Flask
from config import Config
from database import db
from flask_bcrypt import Bcrypt
from routes.exam_routes import exam_bp
from routes.question_routes import question_bp
from flask_jwt_extended import JWTManager
from routes.submission_routes import submission_bp
from routes.violation_routes import violation_bp
# Import models
# Import models
from models.user_model import User
from models.exam_model import Exam
from models.question_model import Question
from models.result_model import Result
from models.student_answer_model import StudentAnswer
from models.violation_model import Violation
# Import blueprints
from routes.auth_routes import auth_bp

app = Flask(__name__)
app.config["JWT_SECRET_KEY"] = "your-super-secret-key"

jwt = JWTManager(app)
bcrypt = Bcrypt(app)
app.config.from_object(Config)

db.init_app(app)

# Register Blueprint
app.register_blueprint(auth_bp, url_prefix="/api/auth")
app.register_blueprint(exam_bp, url_prefix="/api/exams")
app.register_blueprint(
    question_bp,
    url_prefix="/api/questions"
)
app.register_blueprint(
    submission_bp,
    url_prefix="/api/submissions"
)
app.register_blueprint(
    violation_bp,
    url_prefix="/api/violations"
)
@app.route("/")
def home():
    return {
        "message": "AI Remote Proctored Exam Backend Running"
    }


with app.app_context():
    db.create_all()


if __name__ == "__main__":
    app.run(
        host="0.0.0.0",
        port=5000,
        debug=True
    )