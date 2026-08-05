from database import db
from models.exam_model import Exam
from datetime import datetime
from models.question_model import Question

def create_exam(title, subject, duration, total_marks,
                start_time, end_time, created_by):

    exam = Exam(
        title=title,
        subject=subject,
        duration=duration,
        total_marks=total_marks,
        start_time=datetime.fromisoformat(start_time),
        end_time=datetime.fromisoformat(end_time),
        created_by=created_by
    )

    db.session.add(exam)
    db.session.commit()

    return {
        "success": True,
        "message": "Exam Created Successfully"
    }
 
def get_all_exams():

    exams = Exam.query.all()

    exam_list = []

    for exam in exams:
        exam_list.append({
            "id": exam.id,
            "title": exam.title,
            "subject": exam.subject,
            "duration": exam.duration,
            "total_marks": exam.total_marks,
            "start_time": exam.start_time.isoformat(),
            "end_time": exam.end_time.isoformat(),
            "created_by": exam.created_by
        })

    return {
        "success": True,
        "exams": exam_list
    }

def update_exam(exam_id, data):

    exam = db.session.get(Exam, exam_id)

    if not exam:
        return {
            "success": False,
            "message": "Exam not found"
        }

    if "title" in data:
        exam.title = data["title"]

    if "subject" in data:
        exam.subject = data["subject"]

    if "duration" in data:
        exam.duration = data["duration"]

    if "total_marks" in data:
        exam.total_marks = data["total_marks"]

    if "start_time" in data:
        exam.start_time = datetime.fromisoformat(data["start_time"])

    if "end_time" in data:
        exam.end_time = datetime.fromisoformat(data["end_time"])

    db.session.commit()

    return {
        "success": True,
        "message": "Exam Updated Successfully"
    }

def delete_exam(exam_id):

    exam = db.session.get(Exam, exam_id)

    if not exam:
        return {
            "success": False,
            "message": "Exam not found"
        }

    # Delete all questions belonging to this exam
    Question.query.filter_by(exam_id=exam_id).delete()

    # Delete the exam
    db.session.delete(exam)

    db.session.commit()

    return {
        "success": True,
        "message": "Exam Deleted Successfully"
    }