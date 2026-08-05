from database import db
from datetime import datetime


class Result(db.Model):
    __tablename__ = "results"

    id = db.Column(
        db.Integer,
        primary_key=True
    )

    student_id = db.Column(
        db.Integer,
        db.ForeignKey("users.id"),
        nullable=False
    )

    exam_id = db.Column(
        db.Integer,
        db.ForeignKey("exams.id"),
        nullable=False
    )

    score = db.Column(
        db.Integer,
        nullable=False,
        default=0
    )

    total_marks = db.Column(
        db.Integer,
        nullable=False
    )

    submitted_at = db.Column(
        db.DateTime,
        default=datetime.utcnow
    )

    def __repr__(self):
        return (
            f"<Result Student={self.student_id} "
            f"Exam={self.exam_id} "
            f"Score={self.score}>"
        )