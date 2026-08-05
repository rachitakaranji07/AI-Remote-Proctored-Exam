from flask import Blueprint, request, jsonify
from flask_jwt_extended import (
    jwt_required,
    get_jwt,
    get_jwt_identity
)

from database import db
from models.question_model import Question
from models.exam_model import Exam
from models.result_model import Result
from models.student_answer_model import StudentAnswer


submission_bp = Blueprint(
    "submission",
    __name__
)


# =========================================================
# SUBMIT EXAM
# =========================================================

@submission_bp.route("/submit", methods=["POST"])
@jwt_required()
def submit_exam():

    # ---------------------------------
    # CHECK USER ROLE
    # ---------------------------------

    claims = get_jwt()

    if claims.get("role") != "student":
        return jsonify({
            "success": False,
            "message": "Access denied. Student only."
        }), 403

    student_id = int(get_jwt_identity())


    # ---------------------------------
    # GET SUBMISSION DATA
    # ---------------------------------

    data = request.get_json()

    if not data:
        return jsonify({
            "success": False,
            "message": "No submission data provided."
        }), 400

    exam_id = data.get("exam_id")
    answers = data.get("answers", [])


    if not exam_id:
        return jsonify({
            "success": False,
            "message": "Exam ID is required."
        }), 400


    # ---------------------------------
    # CHECK EXAM EXISTS
    # ---------------------------------

    exam = db.session.get(
        Exam,
        exam_id
    )

    if not exam:
        return jsonify({
            "success": False,
            "message": "Exam not found."
        }), 404


    # ---------------------------------
    # PREVENT DUPLICATE SUBMISSION
    # ---------------------------------

    existing_result = Result.query.filter_by(
        student_id=student_id,
        exam_id=exam_id
    ).first()

    if existing_result:
        return jsonify({
            "success": False,
            "message": "You have already submitted this exam."
        }), 409


    # ---------------------------------
    # GET EXAM QUESTIONS
    # ---------------------------------

    questions = Question.query.filter_by(
        exam_id=exam_id
    ).all()

    if not questions:
        return jsonify({
            "success": False,
            "message": "No questions found for this exam."
        }), 404


    # ---------------------------------
    # CONVERT ANSWERS TO MAP
    #
    # question_id -> selected_answer
    # ---------------------------------

    student_answers = {}

    for answer in answers:

        question_id = answer.get(
            "question_id"
        )

        selected_answer = answer.get(
            "selected_answer"
        )

        if (
            question_id is not None
            and selected_answer is not None
        ):

            student_answers[
                int(question_id)
            ] = (
                str(selected_answer)
                .strip()
                .upper()
            )


    # ---------------------------------
    # CALCULATE SCORE
    # AND BUILD REVIEW
    # ---------------------------------

    score = 0
    review = []


    for question in questions:

        selected_answer = (
            student_answers.get(
                question.id
            )
        )

        correct_answer = (
            question.correct_answer
            .strip()
            .upper()
        )


        is_correct = (
            selected_answer is not None
            and selected_answer
            == correct_answer
        )


        if is_correct:

            score += question.marks


        review.append({

            "question_id":
                question.id,

            "question":
                question.question,

            "option_a":
                question.option_a,

            "option_b":
                question.option_b,

            "option_c":
                question.option_c,

            "option_d":
                question.option_d,

            "selected_answer":
                selected_answer,

            "correct_answer":
                correct_answer,

            "is_correct":
                is_correct,

            "marks":
                question.marks
        })


    # ---------------------------------
    # CREATE RESULT
    # ---------------------------------

    result = Result(
        student_id=student_id,
        exam_id=exam_id,
        score=score,
        total_marks=exam.total_marks
    )


    # ---------------------------------
    # SAVE RESULT + STUDENT ANSWERS
    # ---------------------------------

    try:

        # Add result
        db.session.add(result)

        # Generate result.id
        # without committing yet
        db.session.flush()


        # Save answer for every question
        for question in questions:

            selected_answer = (
                student_answers.get(
                    question.id
                )
            )

            student_answer = StudentAnswer(
                result_id=result.id,
                question_id=question.id,
                selected_answer=selected_answer
            )

            db.session.add(
                student_answer
            )


        # Save everything together
        db.session.commit()


    except Exception as e:

        db.session.rollback()

        return jsonify({
            "success": False,
            "message": "Failed to save result."
        }), 500


    # ---------------------------------
    # RETURN RESULT + REVIEW
    # ---------------------------------

    return jsonify({

        "success": True,

        "message":
            "Exam submitted successfully.",

        "result": {

            "result_id":
                result.id,

            "exam_id":
                exam_id,

            "score":
                score,

            "total_marks":
                exam.total_marks,

            "review":
                review
        }

    }), 201


# =========================================================
# GET STUDENT EXAM RESULT
# =========================================================

@submission_bp.route(
    "/result/<int:exam_id>",
    methods=["GET"]
)
@jwt_required()
def get_exam_result(exam_id):

    # ---------------------------------
    # CHECK USER ROLE
    # ---------------------------------

    claims = get_jwt()

    if claims.get("role") != "student":

        return jsonify({
            "success": False,
            "message": "Access denied. Student only."
        }), 403


    student_id = int(
        get_jwt_identity()
    )


    # ---------------------------------
    # FIND RESULT
    # ---------------------------------

    result = Result.query.filter_by(
        student_id=student_id,
        exam_id=exam_id
    ).first()


    # Student has not submitted
    if not result:

        return jsonify({

            "success": True,

            "submitted": False

        }), 200


    # ---------------------------------
    # GET EXAM QUESTIONS
    # ---------------------------------

    questions = Question.query.filter_by(
        exam_id=exam_id
    ).all()


    # ---------------------------------
    # GET SAVED STUDENT ANSWERS
    # ---------------------------------

    saved_answers = StudentAnswer.query.filter_by(
        result_id=result.id
    ).all()


    # Convert saved answers to map
    #
    # question_id -> selected_answer

    student_answers = {}

    for answer in saved_answers:

        student_answers[
            answer.question_id
        ] = answer.selected_answer


    # ---------------------------------
    # BUILD RESULT REVIEW
    # ---------------------------------

    review = []


    for question in questions:

        selected_answer = (
            student_answers.get(
                question.id
            )
        )


        correct_answer = (
            question.correct_answer
            .strip()
            .upper()
        )


        is_correct = (
            selected_answer is not None
            and selected_answer.upper()
            == correct_answer
        )


        review.append({

            "question_id":
                question.id,

            "question":
                question.question,

            "option_a":
                question.option_a,

            "option_b":
                question.option_b,

            "option_c":
                question.option_c,

            "option_d":
                question.option_d,

            "selected_answer":
                selected_answer,

            "correct_answer":
                correct_answer,

            "is_correct":
                is_correct,

            "marks":
                question.marks
        })


    # ---------------------------------
    # RETURN SAVED RESULT
    # ---------------------------------

    return jsonify({

        "success": True,

        "submitted": True,

        "result": {

            "result_id":
                result.id,

            "exam_id":
                result.exam_id,

            "score":
                result.score,

            "total_marks":
                result.total_marks,

            "review":
                review
        }

    }), 200