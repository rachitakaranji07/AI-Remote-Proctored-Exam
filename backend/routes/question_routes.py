from flask import Blueprint, request, jsonify
from flask_jwt_extended import jwt_required, get_jwt
from services.question_service import (
    add_question,
    get_questions_by_exam,
    update_question,
    delete_question
)
question_bp = Blueprint("question", __name__)

@question_bp.route("/add", methods=["POST"])
@jwt_required()
def add():

    claims = get_jwt()

    if claims.get("role") not in ["teacher", "admin"]:
        return jsonify({
            "success": False,
            "message": "Access denied. Teacher or Admin only."
        }), 403

    data = request.get_json()

    result = add_question(
        data["exam_id"],
        data["question"],
        data["option_a"],
        data["option_b"],
        data["option_c"],
        data["option_d"],
        data["correct_answer"],
        data["marks"]
    )

    return jsonify(result)

@question_bp.route("/exam/<int:exam_id>", methods=["GET"])
def get_questions(exam_id):

    result = get_questions_by_exam(exam_id)

    return jsonify(result)

@question_bp.route("/<int:question_id>", methods=["PUT"])
@jwt_required()
def update(question_id):

    claims = get_jwt()

    if claims.get("role") not in ["teacher", "admin"]:
        return jsonify({
            "success": False,
            "message": "Access denied. Teacher or Admin only."
        }), 403

    data = request.get_json()

    result = update_question(question_id, data)

    return jsonify(result)


@question_bp.route("/<int:question_id>", methods=["DELETE"])
@jwt_required()
def delete(question_id):

    claims = get_jwt()

    if claims.get("role") not in ["teacher", "admin"]:
        return jsonify({
            "success": False,
            "message": "Access denied. Teacher or Admin only."
        }), 403

    result = delete_question(question_id)

    return jsonify(result)

@question_bp.route("/exam/<int:exam_id>/student", methods=["GET"])
@jwt_required()
def get_student_questions(exam_id):

    claims = get_jwt()

    if claims.get("role") != "student":
        return jsonify({
            "success": False,
            "message": "Access denied. Student only."
        }), 403

    result = get_questions_by_exam(exam_id)

    if not result.get("success"):
        return jsonify(result)

    safe_questions = []

    for question in result.get("questions", []):
        safe_questions.append({
            "id": question["id"],
            "question": question["question"],
            "option_a": question["option_a"],
            "option_b": question["option_b"],
            "option_c": question["option_c"],
            "option_d": question["option_d"],
            "marks": question["marks"]
        })

    return jsonify({
        "success": True,
        "questions": safe_questions
    })