from database import db


class StudentAnswer(db.Model):
    __tablename__ = "student_answers"

    id = db.Column(
        db.Integer,
        primary_key=True
    )

    result_id = db.Column(
        db.Integer,
        db.ForeignKey("results.id"),
        nullable=False
    )

    question_id = db.Column(
        db.Integer,
        db.ForeignKey("questions.id"),
        nullable=False
    )

    selected_answer = db.Column(
        db.String(1),
        nullable=True
    )

    def __repr__(self):
        return (
            f"<StudentAnswer "
            f"Result={self.result_id} "
            f"Question={self.question_id}>"
        )