from database import db
from datetime import datetime


class Exam(db.Model):
    __tablename__ = "exams"

    id = db.Column(db.Integer, primary_key=True)

    title = db.Column(db.String(150), nullable=False)

    subject = db.Column(db.String(100), nullable=False)

    duration = db.Column(db.Integer, nullable=False)

    total_marks = db.Column(db.Integer, nullable=False)

    start_time = db.Column(db.DateTime, nullable=False)

    end_time = db.Column(db.DateTime, nullable=False)

    created_by = db.Column(db.Integer, nullable=False)

    created_at = db.Column(
        db.DateTime,
        default=datetime.utcnow
    )

    def __repr__(self):
        return f"<Exam {self.title}>"