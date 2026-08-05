from flask import Blueprint, request, jsonify
from flask_jwt_extended import jwt_required, get_jwt, get_jwt_identity
from services.exam_service import (
    create_exam,
    get_all_exams,
    update_exam,
    delete_exam
)
exam_bp = Blueprint("exam", __name__)


@exam_bp.route("/create", methods=["POST"])
@jwt_required()
def create():

    claims = get_jwt()

    if claims.get("role") not in ["teacher", "admin"]:
        return jsonify({
            "success": False,
            "message": "Access denied. Teacher or Admin only."
        }), 403
    created_by = int(get_jwt_identity())
    data = request.get_json()

    result = create_exam(
        data["title"],
        data["subject"],
        data["duration"],
        data["total_marks"],
        data["start_time"],
        data["end_time"],
        created_by
    )

    return jsonify(result)

@exam_bp.route("/", methods=["GET"])
def get_exams():

    result = get_all_exams()

    return jsonify(result)

@exam_bp.route("/<int:exam_id>", methods=["PUT"])
@jwt_required()
def update(exam_id):

    claims = get_jwt()

    if claims.get("role") not in ["teacher", "admin"]:
        return jsonify({
            "success": False,
            "message": "Access denied. Teacher or Admin only."
        }), 403

    data = request.get_json()

    result = update_exam(exam_id, data)

    return jsonify(result) 

@exam_bp.route("/<int:exam_id>", methods=["DELETE"])
@jwt_required()
def delete(exam_id):

    claims = get_jwt()

    if claims.get("role") not in ["teacher", "admin"]:
        return jsonify({
            "success": False,
            "message": "Access denied. Teacher or Admin only."
        }), 403

    result = delete_exam(exam_id)

    return jsonify(result)