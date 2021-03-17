# -*- coding: utf-8 -*-
import csv
import requests
import datetime

# key = 책 제목, value = 재고 수
book_stock_quantity_dict = {}

book_category_dict = {
    "개발": "DEVELOP",
    "경영": "MANAGEMENT",
    "기획": "PLAN",
    "마케팅": "MARKETING",
    "자기계발": "SELF_IMPROVEMENT",
    "자격증": "LICENSE",
    "디자인": "DESIGN",
    "소설": "FICTION",
    "비소설": "NONFICTION"
}


def init_book_stock_quantity_dict():
    book_list_file = open('input/book_list.csv', 'r', encoding='utf-8')
    rows = csv.reader(book_list_file)

    for row in list(rows)[2:]:
        # 정확히 제목을 찾아내기 위해 공백 제거
        title_with_blank_removed = row[1].replace(" ", "")
        stock_count = 0 if row[2] == '예' else 1

        if title_with_blank_removed in book_stock_quantity_dict:
            book_stock_quantity_dict[title_with_blank_removed] += stock_count
        else:
            book_stock_quantity_dict[title_with_blank_removed] = stock_count

    book_list_file.close()


def generate_book_insert_query():
    book_list_file = open('input/book_list.csv', 'r', encoding='utf-8')
    rows = csv.reader(book_list_file)

    # 책 검색 API에 결과가 없는 책
    no_search_result_title_list_file = open("output/no_search_result_title_list.txt", "a")
    no_search_result_title_list_file.truncate(0)

    # 검색 결과와 책 이름이 일치하는 책(대소문자 구별 없음)
    match_title_list_file = open("output/match_title_list.txt", "a")
    match_title_list_file.truncate(0)

    # 검색은 되지만 이름이 다른 책
    mismatch_title_list_file = open("output/mismath_title_list.txt", "a")
    mismatch_title_list_file.truncate(0)

    # 책 정보 insert query
    result_file = open("output/generated_book_insert_query.txt", "a")
    result_file.truncate(0)

    # TODO: key 값 분리하기
    url = 'https://dapi.kakao.com/v3/search/book'
    auth_type = 'KakaoAK'
    key = '3c01aa602b1860ffec4d5a979a78d78b'
    headers = {'Authorization': auth_type + ' ' + key}

    category = ''

    # 책 검색 API 결과와 제목이 정확히 매칭되어, insert 쿼리로 생성된 책 제목 목록
    matched_title_list = []

    for row in list(rows)[2:]:
        title = row[1].strip()

        # 이미 insert 쿼리로 생성된 책은 제외(중복 방지)
        if title in matched_title_list:
            continue

        # category 값 셋팅
        if row[0] != '' and row[0] is not None:
            category = row[0]

        api_url = f'{url}?query={title}&target=title'
        response = requests.get(api_url, headers=headers)
        response.encoding = 'utf-8'
        data = response.json()['documents']

        if len(data) == 0 or data is None:
            no_search_result_title_list_file.write(title + '\n')

        is_matched = False
        for datum in data:
            if title.lower() == datum['title'].lower():
                match_title_list_file.write(title + '\n')

                # author 값 셋팅
                author_str = ''
                for author in datum['authors']:
                    author_str += f'{author},'
                if author_str != '':
                    author_str = author_str[0:len(author_str) - 1]

                # description에서  ' -> \' 로 수정(string 데이터 insert할 때 오류 나지 않도록)
                description = datum["contents"].replace("'", "\\'")

                # insert_query 생성
                insert_query = f'insert into book(isbn, title, description, author, stock_quantity, image_url, publishing_date, registration_date, publisher, category, registrant_id)' \
                               f' values(\'{datum["isbn"].split(" ")[1]}\', \'{datum["title"]}\', \'{description}\', \'{author_str}\', {book_stock_quantity_dict[title.replace(" ", "")]}, \'{datum["thumbnail"]}\', \'{datum["datetime"]}\', now(), \'{datum["publisher"]}\', \'{book_category_dict[category]}\', 1);'
                result_file.write(insert_query + '\n')
                matched_title_list.append(title)

                is_matched = True
                break

        if is_matched is False:
            mismatch_title_list_file.write(title + '\n')

    no_search_result_title_list_file.close()
    match_title_list_file.close()
    mismatch_title_list_file.close()
    result_file.close()
    book_list_file.close()


def generate_rental_insert_query():
    book_list_file = open('input/book_list.csv', 'r', encoding='utf-8')
    rows = csv.reader(book_list_file)

    # 대여 정보 insert query
    result_file = open("output/generated_rental_insert_query.txt", "a")
    result_file.truncate(0)

    for row in list(rows)[2:]:
        if row[2] == '아니오':
            continue

        title = row[1].strip()
        borrower = row[3]
        start_date = datetime.datetime.now()
        end_date = 'null' if row[4] == '장기대여' else "\'" + str(datetime.datetime.now() + datetime.timedelta(days=14)) + "\'"
        return_date = 'null'

        # insert_query 생성
        insert_query = f'insert into rental(end_date, return_date, start_date, status, book_id, member_id)' \
                       f' values({end_date}, {return_date}, \'{start_date}\', \'ON_RENTAL\', (select id from book where title = \'{title}\'), (select id from member where name = \'{borrower}\'));'
        result_file.write(insert_query + '\n')

    result_file.close()
    book_list_file.close()


if __name__ == "__main__":
    init_book_stock_quantity_dict()
    generate_book_insert_query()
    generate_rental_insert_query()
