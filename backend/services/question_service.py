from database import db
from models.question_model import Question
from models.exam_model import Exam


def add_question(exam_id, question, option_a, option_b,
                 option_c, option_d, correct_answer, marks):

    # Check whether the exam exists
    exam = Exam.query.get(exam_id)

    if not exam:
        return {
            "success": False,
            "message": "Exam not found"
        }

    new_question = Question(
        exam_id=exam_id,
        question=question,
        option_a=option_a,
        option_b=option_b,
        option_c=option_c,
        option_d=option_d,
        correct_answer=correct_answer.upper(),
        marks=marks
    )

    db.session.add(new_question)
    db.session.commit()

    return {
        "success": True,
        "message": "Question Added Successfully",
        "question_id": new_question.id
    }
def get_questions_by_exam(exam_id):

    exam = Exam.query.get(exam_id)

    if not exam:
        return {
            "success": False,
            "message": "Exam not found"
        }

    questions = Question.query.filter_by(exam_id=exam_id).all()

    question_list = []

    for q in questions:
        question_list.append({
            "id": q.id,
            "exam_id": q.exam_id,
            "question": q.question,
            "option_a": q.option_a,
            "option_b": q.option_b,
            "option_c": q.option_c,
            "option_d": q.option_d,
            "correct_answer": q.correct_answer,
            "marks": q.marks
        })

    return {
        "success": True,
        "questions": question_list
    }

def update_question(question_id, data):

    question = db.session.get(Question, question_id)

    if not question:
        return {
            "success": False,
            "message": "Question not found"
        }

    if "question" in data:
        question.question = data["question"]

    if "option_a" in data:
        question.option_a = data["option_a"]

    if "option_b" in data:
        question.option_b = data["option_b"]

    if "option_c" in data:
        question.option_c = data["option_c"]

    if "option_d" in data:
        question.option_d = data["option_d"]

    if "correct_answer" in data:
        question.correct_answer = data["correct_answer"].upper()

    if "marks" in data:
        question.marks = data["marks"]

    db.session.commit()

    return {
        "success": True,
        "message": "Question Updated Successfully"
    }

def delete_question(question_id):

    question = db.session.get(Question, question_id)

    if not question:
        return {
            "success": False,
            "message": "Question not found"
        }

    db.session.delete(question)
    db.session.commit()

    return {
        "success": True,
        "message": "Question Deleted Successfully"
    }